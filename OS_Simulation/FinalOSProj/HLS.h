/*
 * HLS.h
 *
 *  Created on: April 16, 2016
 *      Author: Kyle Hagood
 */

#ifndef HLS_H
#define HLS_H
#include <vector>
#include <string>
#include <sstream>
#include "pcb.h"
#include "Dispatcher.h"
#include "MemoryManager.h"

using namespace std;

 class HLS{
 public:
 	void addToNew(pcb* proc);
 	bool run(Dispatcher* disp, MemoryManager* mm);
 	vector<pcb*> getNewProcs(void);
 	int getNewStateSize(void);

 	vector<pcb*> newState;
 };

 #endif
