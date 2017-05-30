#include <vector>
#include "Dispatcher.h"

int main() {
  Dispatcher dispatcher = Dispatcher();
  pcb proc1;
  pcb proc2;
  pcb proc3;
  pcb* runningProc;
  vector<pcb*> list;

  proc1.pid = 1;
  proc2.pid = 2;
  proc3.pid = 3;

  list.push_back(&proc1);
  list.push_back(&proc2);
  list.push_back(&proc3);

  dispatcher.addToReady(list);
  runningProc = dispatcher.run();

  dispatcher.debug();

  dispatcher.moveProcToExit(&proc2);
  dispatcher.run();

  dispatcher.debug();
}
