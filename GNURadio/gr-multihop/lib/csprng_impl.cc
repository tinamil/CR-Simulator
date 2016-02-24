/* -*- c++ -*- */
/* 
 * Copyright 2015 <+YOU OR YOUR COMPANY+>.
 * 
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software; see the file COPYING.  If not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street,
 * Boston, MA 02110-1301, USA.
 */

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include <gnuradio/io_signature.h>
#include "csprng_impl.h"

namespace gr {
  namespace multihop {

    csprng::sptr
    csprng::make(int seed, double freq_rate, double samp_rate)
    {
      return gnuradio::get_initial_sptr
        (new csprng_impl(seed, freq_rate, samp_rate));
    }

    /*
     * The private constructor
     */
    csprng_impl::csprng_impl(int seed, double freq_rate, double samp_rate)
      : gr::sync_block("csprng",
              gr::io_signature::make(0, 0, 0),
              gr::io_signature::make(1, 1, sizeof(int)))
    {
      srand(seed);
      ns_per_hop = BILLION/freq_rate;
      counter = 0;
      //last_update = BASE_TIME;
      last_update = 0;
      time_sync();
    }

    /*
     * Our virtual destructor.
     */
    csprng_impl::~csprng_impl()
    {
    }

    uint64_t csprng_impl::time_sync(){
      //struct timespec now;
      //clock_gettime(CLOCK_MONOTONIC, &now);
      //uint64_t now_ns = (BILLION * (now.tv_sec) + now.tv_nsec);
      //uint64_t elapsed_nanoseconds = now_ns - last_update;
      //uint64_t elapsed_hops = elapsed_nanoseconds / ns_per_hop;
      //if(elapsed_hops > 0){
      //  last_update = now_ns;
      //  for(uint64_t i = 0; i < elapsed_hops; ++i){
      //    counter += 1;
      //  }
      //}
      std::cout<< "CSPRNG: " << counter << std::endl;
      return counter;
    }
    
    uint64_t csprng_impl::next(){
      //counter += 1;
      //return counter;
      return rand();
    }
    
    int
    csprng_impl::work(int noutput_items,
			  gr_vector_const_void_star &input_items,
			  gr_vector_void_star &output_items)
    {
        int *out = (int *) output_items[0];
        for(int i = 0; i < noutput_items; ++i){
          out[i] = (int)next();
        }

        // Tell runtime system how many output items we produced.
        return noutput_items;
    }

  } /* namespace multihop */
} /* namespace gr */

