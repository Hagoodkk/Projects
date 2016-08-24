#include <iostream>
#include "MemoryManager.h"

using namespace std;

MemoryManager::MemoryManager() {
  freeMem = memorySize;
  for (int i = 0; i < memorySize; ++i)
    memory[i] = NULL;
}

/*
 * place a process pointer into our memory array
 *
 */
bool MemoryManager::allocate(pcb* process) {
  int memoryLeftToAllocate = process->ramNeeded;

  //DEBUG PRINT
  //cout << "Running Allocate on process " << process->pid << endl;
  if(memoryLeftToAllocate <= freeMem) {
    for(int i = 0; i < memorySize;) {
      if(memory[i] == NULL) {
        memory[i] = process;
        memoryLeftToAllocate--;
        freeMem--;

        if(memoryLeftToAllocate == 0) {
          //Were done allocating this process so we can return
          //DEBUG PRINT
          //cout << "Process " << process->pid << " successfully allocated." << endl;
          return true;
        }
      }
      i++;
    }
  }

  return false;
}

/*
 * find all occurances of a process pointer in memory and remove them
 * return true if we removed any index of process pointer
 */
bool MemoryManager::deallocate(pcb* process) {
  bool didWeDealloc = false;

  for(int i = 0; i < memorySize; i++) {
    if(memory[i] == process) {
      memory[i] = NULL;
      freeMem++;
      didWeDealloc = true;
    }
  }

  return didWeDealloc;
}

int MemoryManager::getFreeAmount() {
  return freeMem;
}

void MemoryManager::print() {
  for(int i = 0; i < memorySize; i++) {
    if(memory[i] != NULL)
      std::cout << memory[i]->pid << " ";
    else
      std::cout << "~ ";
  }
}

void MemoryManager::debug() {
  std::cout << "\n\nFree Memory = " << freeMem << "\n";
  for(int i = 0; i < memorySize; i++) {
    if(memory[i] != NULL)
      std::cout << i << ": " << memory[i]->pid << '\n';
    else
      std::cout << i << ": EMPTY" << '\n';
  }
}
