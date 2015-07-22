#include <stream.h>
#include <strings.h>
#include "../polka.H"
#include "simple.H"

MyAnimator simple;


int
MyAnimator::Controller()
{
   if (!strcmp(AlgoEvtName,"Init"))
      sv.InitScene(AnimInts[0]);
   else
      sv.CheckInput();
   return(1);
}



/* draw the container and set up scaling values for the weights */

int
SimpleView::InitScene(int param)
{
   int len;

   Create("Simple example animation");

   box = new Rectangle(this,1,0.3,0.2,0.3,0.1,"blue",0.5);
   box->Originate(time);
   
   Action a("MOVE",CLOCKWISE);
   len = box->Program(time,&a);
   time = Animate(time,len);

   cout << "The parameter was " << param << endl;

   return(1);   
}
