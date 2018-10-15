function [a] = set_index(size)
b = randn(3,7,2)
b(42) = 12
b(12) = 20
b(1,7,size) = 20
b(1,7,size) = [20]
a = b(1,7,size)
end