#ifndef MEM_H
#define MEM_H

#include <stddef.h>
#include "pcb.h"

class MemoryManager {

  private:
    const pcb* memory[16];
    const static int memorySize = 16;
    int freeMem;

  public:
    MemoryManager();
    bool allocate(pcb*);
    bool deallocate(pcb*);
    int getFreeAmount();
    void debug();
    void print();
};

#endif
