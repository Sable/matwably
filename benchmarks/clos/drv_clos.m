function drv_clos(scale)
%%
%% Driver for the transitive closure of a directed graph.
%%

N=450;
tic();
B=closure(N);
t = toc();
disp(t)
end

