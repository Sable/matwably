function [a] = get_index(size)
%   disp(size)
  b = randn(3,7,2)
  a = b(42)
  disp(a)
  a = b(12)
  disp(a)
  a = b(3,7,2)
  disp(a)

end