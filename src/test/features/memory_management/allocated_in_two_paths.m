function a = allocated_in_two_paths(c)
    % Is 'd' part of the dynamic set?, is 'd' part of the static set?
    % Is 'd' an input parameter?, Is 'd' an output parameter. (Is this 'd' return
    % in at least one path)
    d = zeros(3,3); % [static_set={d: [d->MallocSite{id:1, count:1,stmt:d=(ones(3,3)),alias:[]}, dynamic_set:{}]
    disp(d)
    
    if c 
        a = c;
    else
        a = ones(3,3);
    end
    disp(a)
    % If join set is non-empty, that is there was no re-definion of
    % variable in one of the paths of execution, add to dynamic set.
    % Since the variable is allocated in all paths, and there is no aliasing 
    % we can statically free variable 'a' before the a=23 redefinition.
    % We treat scalar definitions as non-allocation sites.
    a = 23; % In: static_set={a: [a->MallocSite{id:1, count:1,stmt:a=(ones(3,3)),alias:[]},
            %,a->MallocSite{id:2, count:1,stmt:a=(ones(4,4)),alias:[]} ]
            % },dynamic_set={}
            % Out: static_set={}, dynamic_set={}
            % Statements to free = 
    % Since 'a' here is going to be re-defined, we can free its previous 
    % definitions freely, since there are no aliasing statements and its actually
    % defined along every path.
%     We need to keep track of re-definitions for every variable. If
%     necessary, go back and add the corresponding freeing statements.
    
end