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

#ifndef INCLUDED_MULTIHOP_MULTIHOP_RENDEZVOUS_IMPL_H
#define INCLUDED_MULTIHOP_MULTIHOP_RENDEZVOUS_IMPL_H

#include <multihop/multihop_rendezvous.h>
#include "csprng_impl.h"
#include <vector>
#include <stdexcept>

namespace gr {
  namespace multihop {
    enum State { SEEKING, SYNCING, DONE };
    class multihop_rendezvous_impl : public multihop_rendezvous
    {
     private:
      int max_window_size;
      int num_channels;
      std::vector<int> sliding_window;
      csprng_impl* rng;
      int last_window_update;
      int hop_round;
      int sliding_index;
      int search_rate;
      int finished;
      
      State state;
      int syncing_count; 
      int syncing_success;
      
		  // Number of required hops to be correct in order to synchronize
		  const static long REQUIRED_SYNC_HOPS = 10;

		  // Maximum number of hop attempts to synchronize before aborting
		  const static long MAX_SYNC_HOPS = 20;

      void update_sliding_window();

     public:
      multihop_rendezvous_impl(int seed, double freq_rate, double samp_rate, int num_channels, int window_size, int search_speed);
      ~multihop_rendezvous_impl();
      int next_channel();

      // Where all the action really happens
      int work(int noutput_items,
	       gr_vector_const_void_star &input_items,
	       gr_vector_void_star &output_items);
    };

  } // namespace multihop
} // namespace gr

#endif /* INCLUDED_MULTIHOP_MULTIHOP_RENDEZVOUS_IMPL_H */

