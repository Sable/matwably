function [b] = shapeConstructors(b)
%   Ones constructor calls

  a = ones()
  a = ones([2])
  a = ones([2,2])
  a = ones([1,1])
  a = ones([1])
  a = ones(2)
  a = ones(2,2)
  a = ones(1,1)
  a = ones(1)
  a = ones(1,1,1)
  a = ones(2,2,2)
%   Rand constructor calls

  a = rand()
  a = rand([2])
  a = rand([2,2])
  a = rand([1,1])
  a = rand([1])
  a = rand(2)
  a = rand(2,2)
  a = rand(1,1)
  a = rand(1)
  a = rand(1,1,1)
  a = rand(2,2,2)
  a = randn()
  a = randn([2])
  a = randn([2,2])
  a = randn([1,1])
  a = randn([1])
  a = randn(2)
  a = randn(2,2)
  a = randn(1,1)
  a = randn(1)
  a = randn(1,1,1)
  a = randn(2,2,2)
%   Zero constructor calls
  a = zeros([2,2])
  a = zeros([1,1])
  a = zeros([1])
  a = zeros(2)
  a = zeros(2,2)
  a = zeros(1,1)
  a = zeros(1)
  a = zeros(1,1,1)
  a = zeros(2,2,2)
%   Eye constructor calls

  a = eye()
  a = eye([2])
  a = eye([2,2])
  a = eye([1,1])
  a = eye([1])
  a = eye(2)
  a = eye(2,2)
  a = eye(1,1)
  a = eye(1)
  a = eye(1,1,1)
  a = eye(2,2,2)



end
