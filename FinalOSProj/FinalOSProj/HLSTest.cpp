#include "HLS.h"
#include <vector>
#include <string>
#include "pcb.h"
#include "pcb.cpp"
#include <iostream>

int main(){

	HLS hls;
	vector<pcb> testVect;
	vector<pcb*> testVect2;
	cout << testVect.size() << '\n';
	cout << testVect2.size() << '\n';
	int n = hls.getNewStateSize();
	cout << n;


	return 0;
}