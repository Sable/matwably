function colonConstructor(a)

    % Empty states
    colon(2,2)  %no target
    b = colon([],2) %empty [1,0]
    b = colon(2,[]) %empty [1,0]
    b = colon([],[]) %empty [1,0]
    b = colon([],2,2) %empty [1,0]
    b = colon([],ones(2,1),ones(3,3)) %empty [1,0]
    b = colon([],[],2) %empty [1,0]
    b = colon([],[],[]) %empty [1,0]
    b = colon(-2,-1,2) %empty [1,0]
    b = colon(2,1,-2) %empty [1,0]
%
%    % Same size
    b = colon(4,2,4) %same size [1,1], step positive
    b = colon(4,-2,4) %same size [1,1], step negative
    b = colon(2,2) %same size positive
    b = colon(2,4) %same size positive
    b = colon(-2,-2) %same size negative
%%
%%%    % Actual good use of colon
    b = colon(-4,2,4) %increasing size [1,5], step 2
    b = colon(4,-2,-4) %decreasing size [1,5], step 2
    b = colon(2,-2)  % decreasing [1,5], step 1
    b = colon(-2,2) % increasing [1,5], step 1
%
%
    % arrays and not known scalars fail shape prop.
    c = ones(2,2)
    d = ones(3,4)
    b = colon(c,b,d)
    b = colon(c,b)

    b = colon(a,2,5) % should fail
end