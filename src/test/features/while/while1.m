function [a] = while1(size)
i = 10;
a = ones(10,10);
while a
  i = i+1;
  a = ones(3,3);
  a(3,3) = 0;
  disp(i);
  if i == 20
    break
  end
end
disp(a)
end