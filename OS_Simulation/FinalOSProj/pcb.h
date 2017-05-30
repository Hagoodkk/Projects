/*
 * pcb.h
 *
 *  Created on: Mar 5, 2016
 *      Author: Zac Conner
 */

#ifndef PCB_H
#define PCB_H

#define MAXIOREQ 5

enum PROCESS_STATE
{
	NEW,
	READY,
	RUNNING,
	BLOCKED,
	COMPLETE
};

class pcb
{
public:

	//Constructor
	pcb();

	//----------------------------------------------------------
	//Attributes
	//----------------------------------------------------------
	//Proc Info
	int pid;
	PROCESS_STATE state;
	int entryTime;
	int ramNeeded;
	int totalCycles; //Do we need this for response time?

	//Execution tracking
	int currentExecutionCycle;
	int totalExecutionCycles;

	//IO tracking
	int nextIORequestNumber;
	int currentIORequest;
	int totalIORequests;
	int ioBeginnings [MAXIOREQ];
	int ioRequestLengths [MAXIOREQ];
	int currentIOCycle;
};

#endif
