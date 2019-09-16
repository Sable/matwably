function reductions(a)
    % TODO MISSING STD, MAX, MIN
    % scalar
    a = 4;
%    % for any reduction, a scalar value returns the same value
    f = sum(a) % sum along singleton.
    f = mean(a) % sum along singleton.
    f = prod(a) % sum along singleton.

    f = sum(a,3) % returns a
    f = mean(a,3) % returns a
    f = prod(a,3) % returns a

%
    f = sum(a,1) % returns a
    f = mean(a,1) % return a
    f = prod(a,1) % returns a
%%
%
%    % Matrix
    a = ones(3,1);
    % sum along singleton
    f = sum(a)  % returns [3]
    disp(f)
    f = mean(a) % returns [1]
    disp(f)
    f = prod(a) % returns [1]
    disp(f)
%    f = max(a) % returns [1]
%    f = min(a) % returns [1]
%     larger than dims
    f = sum(a,3) % returns a
    f = mean(a,3) % returns a
    f = prod(a,3) % returns a
%
    f = sum(a,1) % returns [3]
    f = mean(a,1) % returns [1.5]
    f = prod(a,1) % returns [1]
%    f = max(a,1)% returns [1]
%    f = min(a,1)% returns [1]
%
    f = sum(a,2) % returns [1,1,1]
    f = mean(a,2) % returns [1,1,1]
    f = prod(a,2) % returns [1,1,1]
%    f = max(a,2) % returns [1,1,1]
%    f = min(a,2) % returns [1,1,1]
%
%    % Edge cases
    f = sum([]) % returns 0
    f = sum([],3) % returns []
    f = mean([],2) % returns 0
    f = mean([],2) % returns []
    f = prod([],2) % returns 0
    f = prod([],2) % returns []
end