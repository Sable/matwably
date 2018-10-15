function [a] = get_index(size)
  b = randn(3,7,2)
  c = b(42)
  a = b(12)
  a = b(1,7,size)
  a = b([1],7,size)
  a = 4
  %a = a(1)

end