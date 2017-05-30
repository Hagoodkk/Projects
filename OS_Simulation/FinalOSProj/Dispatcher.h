#ifndef DIS_H
#define DIS_H

#include <iostream>
#include <vector>
#include "pcb.h"

using namespace std;

class Dispatcher {
  private:
    pcb* runningProcess;
    std::vector<pcb*> readyQueue;
    std::vector<pcb*> exitList;

  public:
    Dispatcher();
    pcb* run();
    pcb* getRunningProcess();
    void moveProcToExit(pcb*);
    std::vector<pcb*> getExitProcs();
    std::vector<pcb*> getReadyProcs();
    void addToReady(std::vector<pcb*>);
    void debug();
};

#endif
