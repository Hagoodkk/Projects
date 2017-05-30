/*
 * OS Simulator final project for VCU CMSC312. 
 * Group 1:
 * 	Zachary Conner
 * 	Zachary Clute
 * 	Kyle Hagood
 * 	Kenneth Parker
 *
 * MainMethod.cpp (Main program entry point)
 */

#include "MainMethod.h"

using namespace std;

//Declare modules
Dispatcher dispatcher;
MemoryManager memoryManager;
HLS hls;
InterruptManager interruptManager;

int main(){
	vector<pcb*> tmpV;
	bool changeOccured = false;
	bool autoPlay = false;
	pcb* runningJob = NULL;
	int timeSliceTracker = 0;
	int totalCycleTracker = 0;
	char userIn = 'x';
	
	//Initialize modules
	hls = HLS();
	dispatcher = Dispatcher();
	memoryManager = MemoryManager();
	interruptManager = InterruptManager();

	readFile();
	
	//User Instructions
	cout << "Welcome to your OS!" << endl;
	cout << "Your jobs have already been loaded into the system." << endl;
	cout << "Enter 'r' to run until completion." << endl;
	cout << "Or, enter any other character to run until next change." << endl << endl;

	//Execution loop
	while(1)
	{
		//Finishing condition
		if (allProcsFinished())
			break;

		//Increment total execution cycles
		totalCycleTracker++;

		//Get the user input if we aren't autoplaying and a change occured last iteration
		if(!autoPlay && changeOccured){
			cin >> userIn;
			if (userIn == 'r')
				autoPlay = true;
		}
		//Add a delay if we are autoplaying and a change occured
		else if(changeOccured)
		{
			delay();
		}
		
		//Reset changeOccured bool
		changeOccured = false;

		//Step 1: Run HLS
		//Bring in new procs
		for (int i = 0; i < masterArray.size(); i++){
			if (masterArray.at(i).entryTime == totalCycleTracker){
				hls.addToNew(&(masterArray.at(i)));
				changeOccured = true;
			}
		}

		//Run HLS to move new procs to ready
		if(hls.run(&dispatcher, &memoryManager))
			changeOccured = true;

		//Step 2: Check for interrupts
		//Run Interrupt manager
		//Gets all jobs that have moved from blocked to ready and passes them to dispatcher
		tmpV = interruptManager.run();
		if(!tmpV.empty()){
			//DEBUG print
			//cout << "Interrupt found! At least one proc has finished IO." << endl;
			dispatcher.addToReady(tmpV);
			changeOccured = true;
		}
		else{
			//DEBUG Print
			//cout << "No interrupts found." << endl;
		}

		//Step 3: Check if we have a running proc
		//If not, get one from dispatcher and mark as the beginning of its time slice
		if(runningJob == NULL){
			//DEBUG print
			//cout << "Running Dispatcher" << endl;
			runningJob = dispatcher.run();
			if(runningJob){
				timeSliceTracker = 0;
				changeOccured = true;
				//DEBUG print
				//cout << "Dispatcher returned process " << runningJob->pid << endl;
			}
			else{
				//DEBUG print
				//cout << "Dispatcher.run() returned NULL" << endl;
			}
		}
		
		//Step 4: Execute the running proc if we have one
		if(runningJob){
			//DEBUG print
			//cout << "Executing process " << runningJob->pid << endl;
			runningJob->currentExecutionCycle++;
			timeSliceTracker++;

			//Check for IO Req
			//If so, give to Interrupt manager
			if (runningJob->ioBeginnings[runningJob->nextIORequestNumber-1] == runningJob->currentExecutionCycle)
			{
				interruptManager.addToBlocked(runningJob);
				runningJob = NULL;
				timeSliceTracker = 0;
				changeOccured = true;
			}
		}

		//Step 5: Check to see if current running proc has now finished.
		//If so, move it to exit
		if(runningJob){
			if (runningJob->currentExecutionCycle >= runningJob->totalExecutionCycles)
			{
				//DEBUG print
				//cout << "Process " << runningJob->pid << " has completed!" << endl;
				runningJob->state = COMPLETE;
				dispatcher.moveProcToExit(runningJob);
				memoryManager.deallocate(runningJob);
				runningJob = NULL;
				changeOccured = true;
				timeSliceTracker = 0;
			}
			//Else, if time sliced, give back to dispatcher
			else if(timeSliceTracker >= TIMESLICE){
				//DEBUG print
				//cout << "Process " << runningJob->pid << " has reached end of timeslice!" << endl;
				dispatcher.addToReady(vector<pcb*>(1,runningJob));
				runningJob = NULL;
				timeSliceTracker = 0;
				changeOccured = true;
			}
		}

		//Only print output if a change occured
		if(changeOccured) printOutput( runningJob, totalCycleTracker );
	}
	cout << "Execution Complete" << endl;
	return 0;
}

void readFile(){

	ifstream readFile;
	string fileLine;
	string delim = ",";
	string temp;
	int temp2;
	int jobCount = 0;
	int lineCount = 0;
	vector<int> ioBeginnings;
	vector<int> ioRequestLengths;
	int lastEntryTime = 0;
	int lastIOBeginning;

	vector<int> jobData;
	//0.entryTime, 1.ramNeeded, 2.totalCycles, 3-7.ioBeginnings,8-12.ioRequestLengths
	//the 'number.' part refers to the index of the value
	//entries in file will be separated with a comma and end with a comma as well

	

	readFile.open("Input.txt");

	while(!readFile.eof() && jobCount < 60){
		lineCount++;
		jobData.clear();
		ioRequestLengths.clear();
		ioBeginnings.clear();
		lastIOBeginning = 0;
		bool goodEntry = true;

		// Read in the entire line
		getline(readFile,fileLine);

		// Parse each line with ',' delimeter, then push each number into jobData vector
		for(int i = 0; i < 13; i++){
			if (fileLine.find(delim) != string::npos){
				temp = fileLine.substr(0,fileLine.find(delim));
				fileLine = fileLine.substr(fileLine.find(delim)+1,fileLine.length());
				temp2 = atoi(temp.c_str());
				jobData.insert(jobData.end(),temp2);
			}
		}

		//Set the ioBeginnings and ioRequestLengths vectors from file input
		for (int i = 3; i <= 7; i ++){
			if (jobData.at(i) > lastIOBeginning){ 
				ioBeginnings.insert(ioBeginnings.end(),jobData.at(i));
				lastIOBeginning = jobData.at(i);
			}
		}
		for (int i = 8; i <= 12; i++){
			if (jobData.at(i) >= 25 && jobData.at(i) <= 50) 
				ioRequestLengths.insert(ioRequestLengths.end(),jobData.at(i));
		}


		//Checking input for validity
		if (jobData.size() != 13){
			cout << "Invalid number of entries in job data." << endl;
			goodEntry = false;
		}
		if (!(jobData.at(0) > 0)){
			cout << "Invalid entry. Entry time must be greater than 1." << endl;
			goodEntry = false;
		}
		if (!(jobData.at(0) > lastEntryTime)){
			cout << "Invalid entry. Entry time must be greater than all previous entry times." << endl;
			goodEntry = false;
		}
		if (!(jobData.at(1) >= 1 && jobData.at(1) <= 8)){
			cout << "Invalid entry for amount of ram needed." << endl;
			goodEntry = false;
		}
		if (!(jobData.at(2) >= 10 && jobData.at(2) <= 950)){
			cout << "Invalid entry. Total execution cycles must be between 10 and 950." << endl;
			goodEntry = false;
		}
		if (ioBeginnings.size() != ioRequestLengths.size()){
			cout << "Invalid entry. Number of IO Beginnings does not equal number of IO Request Lengths." << endl;
			goodEntry = false;
		}


		// After jobData vector is constructed for an individual line,
		// check values for errors before adding job's PCB to masterArray
		if (goodEntry == true){

			//Add job's PCB to Master Array
			masterArray.insert(masterArray.end(),pcb());

			//Set PCB variables
			masterArray.at(jobCount).pid = jobCount;
			masterArray.at(jobCount).entryTime = jobData.at(0);
			masterArray.at(jobCount).ramNeeded = jobData.at(1);
			masterArray.at(jobCount).totalExecutionCycles = jobData.at(2);
			masterArray.at(jobCount).currentExecutionCycle = 0;
			//masterArray.at(jobCount).totalExecutionCycles = jobData.at(3);

			// Set ioBeginnings and ioRequestLengths to values or -1 if not used
			if (ioBeginnings.size() > 0){
				masterArray.at(jobCount).nextIORequestNumber = 1;
			}
			else{
				masterArray.at(jobCount).nextIORequestNumber = 0;
			}
			masterArray.at(jobCount).currentIORequest = 0;
			masterArray.at(jobCount).totalIORequests = ioBeginnings.size();
			
			for (int i = 0; i < 5; i++){
				if (ioBeginnings.size() > i){
					masterArray.at(jobCount).ioBeginnings[i] = ioBeginnings.at(i);
				}
				else{
					masterArray.at(jobCount).ioBeginnings[i] = -1;
				}
				if (ioRequestLengths.size() > i){
					masterArray.at(jobCount).ioRequestLengths[i] = ioRequestLengths.at(i);
				}
				else{
					masterArray.at(jobCount).ioRequestLengths[i] = -1;
				}
			}
			masterArray.at(jobCount).currentIOCycle = -1;

			//Update lastEntryTime to ensure sequential job entry
			lastEntryTime = jobData.at(0);
			
			

			
			jobCount++;
		}
		else{
			cout << "Line " << lineCount << " not accepted. See System docs for instructions on proper job input." << endl;
	}



		// Comment this in for debugging the input
		//
		//for (int i = 0; i < jobData.size(); i++){
		//	cout << jobData.at(i) << ',';
		//}
		//cout << '\n';
		//		
		
	}
	
	readFile.close();
}

string makeString( vector<pcb*> list ){
	string returnStr;

	string temp;
	stringstream out;

	for (vector<pcb*>::iterator i = list.begin(); i != list.end(); ++i){
		out << (*i)->pid;
		temp = out.str();
		returnStr += temp + " ";
		out.str("");
		out.clear();
	}
	return returnStr;
}

vector<pcb*>::iterator next(vector<pcb*>::iterator iter)
{
    return ++iter;
}

string currentStatus( vector<pcb*> list ){
	string returnStr;

	string temp;
	stringstream out;

	for (vector<pcb*>::iterator i = list.begin(); i != list.end(); ++i){
			out << (*i)->pid;
			temp = out.str();
			returnStr += temp + "           ";

			out.str("");
			out.clear();
			out << (  (*i)->totalExecutionCycles - (*i)->currentExecutionCycle  );
			temp = out.str();
			returnStr += temp + "                     ";

			out.str("");
			out.clear();
			out <<   (*i)->currentExecutionCycle  ;
			temp = out.str();
			returnStr += temp + "               ";

			out.str("");
			out.clear();
			out << (  (*i)->currentIORequest   ) ;
			temp = out.str();
			returnStr += temp + "               ";

			out.str("");
			out.clear();
			out << ( (*i)->totalIORequests - (*i)->currentIORequest );
			temp = out.str();
			returnStr += temp ;

			out.str("");
			out.clear();
			out << endl;
			temp = out.str();
			returnStr += temp ;
			
			if( next(i) != list.end() ){
				out.str("");
				out.clear();
				out << "     ";
				temp = out.str();
				returnStr += temp ;
			}
		}


	return returnStr;
}


void printOutput( pcb* runningJob, int totalCycleTracker ){
	int i;
	int runningPid = -1;
	cout << endl << "INFO DISPLAY" << endl; 
	cout << "Total CPU Cycles: " << totalCycleTracker << endl;

	cout << "                         Resident Set of Processes" << endl;
	cout << "Memory Status:        0 1 2 3 4 5 6 7 8 9 A B C D E F" << endl;
	cout << "                      "; memoryManager.print();
	// Zach's processes in memory location string
 
	cout << endl << endl;
	cout << "STATES" << endl;
	cout << "New:       " << makeString( hls.getNewProcs() ) << endl;						// string from Kyle's HLS
	cout << "Ready:     " << makeString( dispatcher.getReadyProcs() ) << endl;		// vector from Zach's MemoryManager
	
	if(runningJob)
		runningPid = runningJob->pid;
	
	if(runningPid != -1) cout << "Running:   " << runningPid << endl;					// from main
    else cout << "Running:" << endl;
    
	cout << "Blocked:   " << makeString( interruptManager.getBlockedProcs() ) << endl;	// vector from ZAC's InterruptManager
	cout << "Exit:      " << makeString( dispatcher.getExitProcs() ) << endl << endl;		// vector from Zach's 

	cout << "          " << "Process     Cycles to Complete     Cycles Used     IO Finished     IO Left" << endl;
	if(runningJob){	
		cout << "Running:  " ;
		cout << runningJob->pid << "           " << runningJob->totalExecutionCycles - runningJob->currentExecutionCycle << "                     " ;
		cout << runningJob->currentExecutionCycle << "               " << runningJob->currentIORequest << "               ";
		cout << runningJob->totalIORequests - runningJob->currentIORequest << endl;
	}
    else cout << "Running:" << endl;
    
	cout << "Ready:    " ;
	cout << currentStatus( dispatcher.getReadyProcs() ) << endl;

	cout << "Blocked:  " ;
	cout << currentStatus( interruptManager.getBlockedProcs() ) << endl;
	cout << endl << endl << endl;
    if(runningPid != -1) cout << endl;
    
}

//This returns a bool saying whether or not all procs have finished.
//It just loops through each one in the master array and checks it.
bool allProcsFinished(void){
	for (std::vector<pcb>::iterator i = masterArray.begin(); i != masterArray.end(); ++i)
	{
		if((*i).state != COMPLETE)
			return false;	
	}
	return true;
}

//Made my own delay function using a loop and counter.
void delay(void)
{
	int count = 0;
	while(1)
	{
		if (count >= 500000000)
		{
			break;
		}
		else
		{
			count++;
		}
	}
}
