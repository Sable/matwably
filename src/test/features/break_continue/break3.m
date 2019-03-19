function [a] = break3(b)
c = ones(3,3)
% FOR LOOP 2 Decreasing
for v = 8:-2:0
  for j = 1:10
    disp(j)
    if j == 3 && v == 4
      disp(20)
      disp(30)
      break;
    end
  end
end
a = 3;
end