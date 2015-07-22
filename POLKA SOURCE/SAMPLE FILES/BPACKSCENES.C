#include <stream.h>
#include <strings.h>
#include "../polka.H"
#include "bpack.H"

#define  BIN_WD    0.6
#define  BIN_HT    0.5


MyAnimator bpack;


int
MyAnimator::Controller()
{
   if (!strcmp(AlgoEvtName,"Init"))
      b.Init(AnimInts[0]);
   else if (!strcmp(AlgoEvtName,"NewWeight"))
      b.NewWeight(AnimInts[0],AnimDoubles[0]);
   else if (!strcmp(AlgoEvtName,"Failure"))
      b.MoveTo(AnimInts[0],AnimInts[1]);
   else if (!strcmp(AlgoEvtName,"Success")) { 
      b.MoveTo(AnimInts[0],AnimInts[1]);
      b.InPlace(AnimInts[0],AnimInts[1]);
   }
   else
      b.CheckInput();
   return(1);
}



/* draw the container and set up scaling values for the weights */

int
Blocks::Init(int bins)
{
   Loc1Desc line;
   int len,d;

   Create("First-fit bin packing");

			/* draw the bin holding area */
   Rectangle *r = new Rectangle(this,1,0.3,0.2,BIN_WD,BIN_HT,"black",0.0);
   r->Originate(0);
   
   wid = BIN_WD / bins;

   appear = new Loc(0.05,0.2);  /* save place for new elt to appear */

   line.num = bins+1;  // we want them left-adjusted, so need to add one
   line.fx = 0.3; line.fy = 0.2;
   line.tx = 0.3+BIN_WD; line.ty = 0.2;
   line.Make(spots);
	/* array of bin locations (where next weight should go) */

   Action a("DELAY",5);
   len = r->Program(0,&a);
   time = Animate(time,len);

   return(1);   
}




/* have a new weight (with height scaled to value) appear */

int
Blocks::NewWeight(int idnum, double val)
{
   double  ht,x,y,cx,cy;
   char    str[5];
   Loc     *center;
   int     len;

   ht = val * BIN_HT;	/* scale how big the rect should be */

   x = appear->XCoord();
   y = appear->YCoord();
   weights[idnum] = new Rectangle(this,1,x,y,wid,ht,"red",0.5);
   weights[idnum]->Originate(time);
   center = weights[idnum]->Where(PART_C);
   cx = center->XCoord();
   cy = center->YCoord();

   sprintf(str,"%d",idnum);
   labels[idnum] = new Text(this,1,cx,cy,"black",NULL,str,1);
   labels[idnum]->Originate(time);

   Action a("DELAY",1);
   len = weights[idnum]->Program(time,&a);
   time = Animate(time,len);

   return(1);
}





/* move the weight to the corresponding bin position */

int
Blocks::MoveTo(int n, int binnum)
{
   int len;

   Loc *frompt = weights[n]->Where(PART_SW);
   Loc *topt = spots[binnum];
   Action a("MOVE",frompt,topt,CLOCKWISE);
   len = weights[n]->Program(time,&a);
   len = labels[n]->Program(time,&a);
   time = Animate(time,len);
   delete(frompt);
   return(1);
}




/* make the weight move to the given bin, then highlight it to show it */
/* found its place						       */

int
Blocks::InPlace(int n, int binnum)
{
   double	  x0,y0,x1,y1;
   double	  fval = 1.0;
   int            len;

   Loc *nw = weights[n]->Where(PART_NW);  /* put a black outline 
                                             around weight */
   x0 = nw->XCoord();
   y0 = nw->YCoord();

   LocPtr se = weights[n]->Where(PART_SE);
   x1 = se->XCoord();
   y1 = se->YCoord();
   Rectangle *r =  new Rectangle(this,1,x0,y1,x1-x0,y0-y1,"black",0.0);
   r->Originate(time);

   Action a("FILL",1,&fval,&fval);
   len = weights[n]->Program(time,&a);
   time = Animate(time,len);

    /* reset this bin's entry position to the top of the weight */
   delete(spots[binnum]); 
   spots[binnum] = nw;   

   delete(se);
   return(1);
}
