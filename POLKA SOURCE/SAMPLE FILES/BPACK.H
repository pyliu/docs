class Blocks : public View {
 public:
   int Init(int);
   int NewWeight(int,double);
   int MoveTo(int,int);
   int InPlace(int,int);

 private:
   Rectangle *weights[100];
   Text *labels[100];
   Loc *appear, *spots[100];
   double wid;
 };


class MyAnimator : public Animator {
  public:
   int Controller();
  private:
   Blocks b;
};


