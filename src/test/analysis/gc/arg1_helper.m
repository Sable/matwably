function [a,b,c] = arg1_helper(d,e,f,h)
    % d and f are set as external dynamic
    % sites, e is ignored.
    %

    r = rand(3) %static variable to be freed
    a = d % dynamic count of e increases,
          % a added to dynamic set
    b = 12
    c = zeros(2)

    % Here statements 'r' have to be freed. while
    % c must have an initial dynamic count now of 1
    % a must not be touched as it is definitely external
    % at the end of the function
end