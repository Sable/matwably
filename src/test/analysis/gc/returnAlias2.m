% d -> [DOUBLE&1*1&REAL]
function [ a ] = returnAlias2(d)
    % d is a scalar, no need to GC.

    a = rand(2,2)
    % increase counter of site:
    % {rand(2,2): 1}
    b = a
    % increase reference of site:
    % {rand(2,2): 2}

    % Implicit return in Matlab
    % Set rc to 0 for return site a,
    % {rand(2,2): 0}.
    % (No variable references to outside
    % and no globals or persistent variables)
    % Since b refers the same site as a,
    % do not free b.
end

