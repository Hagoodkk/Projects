#ifndef INTERRUPTMANAGER_H
#define INTERRUPTMANAGER_H

#include "pcb.h"
#include <vector>
#include <iostream>

class InterruptManager
{
	public:
		InterruptManager();
				
		std::vector<pcb*> run(void);
		void addToBlocked(pcb* job);
		std::vector<pcb*> getBlockedProcs(void);

	private:
		std::vector<pcb*> blockedQueue;
};
#endif