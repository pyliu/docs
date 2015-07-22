#include <stream.h>
#include <strings.h>
#include "../polka.H"
#include "simple.H"

extern MyAnimator simple;

main(int argc, char *argv[])
{
   simple.RegisterAlgoEvt("Init","d");
   simple.RegisterAlgoEvt("WAIT",NULL);

   simple.SendAlgoEvt("Init",7);

   while (1)
     simple.SendAlgoEvt("WAIT");
}

