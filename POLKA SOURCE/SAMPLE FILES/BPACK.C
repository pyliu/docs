#include <stream.h>
#include <strings.h>
#include "../polka.H"
#include "bpack.H"

extern MyAnimator bpack;

main(int argc, char *argv[])
{
   int n,b,i,wtnum;
   double wt;
   double bin[100];

   bpack.RegisterAlgoEvt("Init","d");
   bpack.RegisterAlgoEvt("NewWeight","df");
   bpack.RegisterAlgoEvt("Failure","dd");
   bpack.RegisterAlgoEvt("Success","dd");
   bpack.RegisterAlgoEvt("WAIT",NULL);

   printf("How many bins?\n");
   scanf("%d",&n);
   bpack.SendAlgoEvt("Init",n);

   for (i=0; i<n; ++i)
      bin[i] = 0.0;

   wtnum = 0;
   printf("Enter the weights (0.0 to quit)\n");
   for (;;)
      { scanf("%lf",&wt);
	if (wt == 0.0) break;
	bpack.SendAlgoEvt("NewWeight",wtnum,wt);
	b = 0;
	while (bin[b] + wt > 1.0)
	   { bpack.SendAlgoEvt("Failure",wtnum,b);
	     b++;
	   }
	bpack.SendAlgoEvt("Success",wtnum,b);
	bin[b] += wt;
	wtnum++;
      }
   while (1)
     bpack.SendAlgoEvt("WAIT");
}
