function [a] = break4(b)
for i=2:b
  prime = 1;
  for j= 2:sqrt(i)
      if (mod(i,j) == 0)
        break;
      end
  end
end
a = 3;
end