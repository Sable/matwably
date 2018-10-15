function [a] = get_general(size)
b = randn(3,7,2)
c = b([1,2,3],2,[3])
a = b(:, 2,2)
a = b(:,[2,2],[2])
a = b(:)
end