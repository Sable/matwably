function binary1(size)
  mmc_t1 = 1;
  A = rand(mmc_t1, size);
  mmc_t2 = 100;
  mmc_t0 = mtimes(mmc_t2, A);
  A = floor(mmc_t0);
end