/*
 * HLS.cpp
 *
 *  Created on: Mar 26, 2016
 *      Author: Kyle Hagood
 */

#include "HLS.h"

/*class HLS
{
public:*/

	//Add job to New State
	void HLS::addToNew(pcb* proc){
		//cout << "Adding proc " << proc->pid << " to NEW" << endl;
		proc->state = NEW;
		newState.insert(newState.end(),proc);
	}

	//If memoryManager has enough memory, add the job to Ready
	bool HLS::run(Dispatcher* disp, MemoryManager* mm){
		vector<pcb*> temp;
		for (int i = 0; i < newState.size(); i++){
			if (mm->allocate(newState.at(i))){
				temp.insert(temp.end(),newState.at(i));
			 	newState.erase(newState.begin()+i);
			}
		}
		disp->addToReady(temp);
		if (temp.empty())
			return false;
		else
			return true;
	}

	//Print stats for jobs in newState vector
	vector<pcb*> HLS::getNewProcs(void){
		return newState;
	}

	int HLS::getNewStateSize(void){
		return newState.size();
	}
		
//};
