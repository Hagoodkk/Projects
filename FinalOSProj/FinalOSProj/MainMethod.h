#ifndef MAINMETHOD_H
#define MAINMETHOD_H

#include <fstream>
#include <iostream>
#include <sstream>
#include <vector>
#include <stdlib.h>
#include "pcb.h"
#include "MemoryManager.h"
#include "InterruptManager.h"
#include "Dispatcher.h"
#include "HLS.h"

//Globals
vector<pcb> masterArray;
const int TIMESLICE = 10;

//Function prototypes
void readFile();
void printOutput(pcb* runningJob, int timeSliceTracker);
bool allProcsFinished(void);
void delay(void);
#endif
