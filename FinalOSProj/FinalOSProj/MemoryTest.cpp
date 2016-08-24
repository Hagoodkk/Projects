#include "MemoryManager.h"

int main() {
  MemoryManager memMan = MemoryManager();
  pcb proc1;
  pcb proc2;
  pcb proc3;

  proc1.pid = 1;
  proc1.ramNeeded = 3;
  proc2.pid = 2;
  proc2.ramNeeded = 4;
  proc3.pid = 3;
  proc3.ramNeeded = 14;

  memMan.debug();
  memMan.allocate(&proc1);
  memMan.allocate(&proc2);
  memMan.allocate(&proc3);


 // memMan.debug();

  memMan.deallocate(&proc2);

//  memMan.debug();

  memMan.print();

  return 0;
}
