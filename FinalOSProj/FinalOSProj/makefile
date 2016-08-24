CC = g++
DEBUG = -g
SOURCES = MainMethod.cpp pcb.cpp InterruptManager.cpp MemoryManager.cpp Dispatcher.cpp HLS.cpp
OBJS = $(SOURCES:.cpp=.o)

all: simulator

simulator: $(OBJS)
	$(CC) $(DEBUG) -o $@ $(OBJS) 

.cpp.o: 
	$(CC) $(DEBUG) -c $<

clean: 
	rm -rf $(OBJS)
	rm -rf *.swp
#$(OBJS): pcb.h MainMethod.h
