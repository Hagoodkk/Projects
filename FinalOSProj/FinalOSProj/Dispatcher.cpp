#include "Dispatcher.h"

Dispatcher::Dispatcher() {
}

/*
 * Get a new process and set it to running
 * if addToReady hasn't been called yet, throw an error
 */
pcb* Dispatcher::run() {
  try {
    if(!readyQueue.empty()) {
      runningProcess = readyQueue.front();
      readyQueue.erase(readyQueue.begin());
      return runningProcess;

    } else {throw "The dispatcher readyQueue is empty";}

  } catch(const char* msg) {
    //std::cerr << msg << endl;
    return NULL;
  }
}

/*
 * Get the currently running process
 */
pcb* Dispatcher::getRunningProcess() {
  return runningProcess;
}

/*
 * Move a currently running process to exti
 * We don't really need to pass a process but 
 * i'll put checks for now.
 */
void Dispatcher::moveProcToExit(pcb* process) {
  try {
    if(runningProcess == process) {
      exitList.push_back(process);
      runningProcess = NULL;
    }
    else {throw "the process passed isn't the current running process";}
  } catch(const char* msg) {
    std::cerr << msg << endl;
  }
}

/*
 * Get the current readyQueue
 */
vector<pcb*> Dispatcher::getReadyProcs() {
  return readyQueue;
}

/*
 * get a list of procs that have exited
 */
vector<pcb*> Dispatcher::getExitProcs() {
  return exitList;
}

/*
 * add a list of processes to the readyQueue
 * This must be called once before calling run
 */
void Dispatcher::addToReady(std::vector<pcb*> addToQueue) {
  
  for (std::vector<pcb*>::iterator i = addToQueue.begin(); i != addToQueue.end(); ++i){
    (*i)->state = READY;
    //DEBUG Print
    //cout << "Adding process " << (*i)->pid << " to READY" << endl;
  }

  readyQueue.insert(readyQueue.end(), addToQueue.begin(), addToQueue.end());
}

void Dispatcher::debug() {
  std::cout << "\n\nrunning process = " << runningProcess->pid << endl << "current readyQueue: " << endl;
  for(std::vector<pcb*>::iterator i = readyQueue.begin(); i != readyQueue.end(); i++) {
    cout << (*i)->pid << endl;
  }
  std::cout << "\nExit list:" << endl;
  for(std::vector<pcb*>::iterator i = exitList.begin(); i != exitList.end(); i++) {
    cout << (*i)->pid << endl;
  }
}
