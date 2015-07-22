class SimpleView : public View {
 public:
   int InitScene(int);

 private:
   Rectangle *box;
 };


class MyAnimator : public Animator {
  public:
   int Controller();
  private:
   SimpleView sv;
};
