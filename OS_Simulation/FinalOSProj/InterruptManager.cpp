#include "InterruptManager.h"
using namespace std;
InterruptManager::InterruptManager(){};

std::vector<pcb*> InterruptManager::run(void){
	std::vector<pcb*> completedJobs;

	//check blockedQueue for any jobs that need to be moved to ready
	for (std::vector<pcb*>::iterator i = blockedQueue.begin(); i != blockedQueue.end();)
	{
		//If the last IO cycle of this IO request
		if ((*i)->currentIOCycle == (*i)->ioRequestLengths[(*i)->currentIORequest-1])
		{
			//cout << "InterruptManager: Moving pid " << (*i)->pid << " to READY" << endl;
			(*i)->state = READY;
			//Add to vector of jobs that need to be returned to ready queue
			completedJobs.push_back((*i));
			//erase from blockedQueue and get new iterator
			i = blockedQueue.erase(i);
		}
		else
		{
			//Increment currentIOCycle
			(*i)->currentIOCycle++;
			//Increment iterator
			++i;
		}
	}
	return completedJobs;
}

void InterruptManager::addToBlocked(pcb* job){
	//DEBUG print
	//cout << "Adding process " << job->pid << " to BLOCKED" << endl;
	job->state = BLOCKED;
	job->nextIORequestNumber++;
	job->currentIORequest++;
	job->currentIOCycle = 0;
	blockedQueue.push_back(job);
}

std::vector<pcb*> InterruptManager::getBlockedProcs(void){
	return blockedQueue;
}