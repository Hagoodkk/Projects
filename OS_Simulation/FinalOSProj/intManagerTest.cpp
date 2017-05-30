#include <iostream>
#include <vector>
#include <stdlib.h>
#include "InterruptManager.h"
#include "pcb.h"

using namespace std;

int main(void)
{
	InterruptManager intMan = InterruptManager();
	std::vector<pcb*> v, fin;
	int count = 0;
	pcb t1 = pcb();
	pcb t2 = pcb();
	t1.pid = 1;
	t2.pid = 0;
	t1.currentIORequest = 0;
	t1.nextIORequestNumber = 0;
	t1.ioRequestLengths[0] = 3;
	t2.currentIORequest = 0;
	t2.nextIORequestNumber = 0;
	t2.ioRequestLengths[0] = 1;
	intMan.addToBlocked(&t1);
	intMan.addToBlocked(&t2);
	while(1){
		cout << "Cycle: " << count << '\n';
		fin = intMan.run();
		for (std::vector<pcb*>::iterator x = fin.begin(); x != fin.end(); ++x)
		{
			cout << "Finished: " << (*x)->pid << '\n';
		}
		v = intMan.getBlockedProcs();
		
		if (v.empty())
			break;

		for (std::vector<pcb*>::iterator i = v.begin(); i != v.end(); ++i)
		{
			cout << "Blocked: " << (*i)->pid << '\n';
		}
		count++;
	}
	
	return 0;
}