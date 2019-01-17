function [] = f2(c)
  a = 3;
  b = 5;
  mc_t7 = a < b;
  while mc_t7
    mc_t2 = 1;
    mc_t1 = uminus(mc_t2);
    mc_t4 = eq(b, mc_t1);
    if mc_t4
      disp(b); % [] = ...
    end
    mc_t5 = 1;
    a = minus(a, mc_t5);
    mc_t6 = 2;
    b = minus(a, mc_t6);
    mc_t7 = le(a, b);
  end
  f3(a)
end
