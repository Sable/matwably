function [a] = for8(b)
a = ones(4,2);
c = 5.*ones(4,2);
disp(c)
% FOR LOOP INCREASING UNKNOWN
for v = a:b:c
  disp(v)
end
a = 3
end