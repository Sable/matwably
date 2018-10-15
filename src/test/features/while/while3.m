function [a] = while3(size)
% WHILE LOOP 3
 limit = 0.8;
 s = 0;
 while 1
   tmp = rand;
   if tmp > limit
    break
   end
   s = s + tmp;
 end
a = 3
end