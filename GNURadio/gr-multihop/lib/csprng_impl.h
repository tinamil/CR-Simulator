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

#ifndef INCLUDED_MULTIHOP_CSPRNG_IMPL_H
#define INCLUDED_MULTIHOP_CSPRNG_IMPL_H

#include <multihop/csprng.h>
#include <limits.h>
#include <ctime>

#define BILLION 1000000000L

namespace gr {
  namespace multihop {

    class csprng_impl : public csprng
    {
     private:
      uint64_t counter;
      const static uint64_t BASE_TIME = 12476075133901;
      uint64_t last_update;
      uint64_t ns_per_hop;
      uint64_t samps_per_hop;
      uint64_t samples;
      
      uint64_t time_sync();

     public:
      csprng_impl(int seed, double freq_rate, double samp_rate);
      ~csprng_impl();
      uint64_t next();

      // Where all the action really happens
      int work(int noutput_items,
	       gr_vector_const_void_star &input_items,
	       gr_vector_void_star &output_items);
    };

  } // namespace multihop
} // namespace gr

#endif /* INCLUDED_MULTIHOP_CSPRNG_IMPL_H */

