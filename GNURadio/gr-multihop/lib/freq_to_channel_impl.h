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

#ifndef INCLUDED_MULTIHOP_FREQ_TO_CHANNEL_IMPL_H
#define INCLUDED_MULTIHOP_FREQ_TO_CHANNEL_IMPL_H

#include <multihop/freq_to_channel.h>
#include <cmath>

namespace gr {
  namespace multihop {

    class freq_to_channel_impl : public freq_to_channel
    {
     private:
      double channel_fft_ratio;

     public:
      freq_to_channel_impl(int fft_size, double sample_rate, int num_channels);
      ~freq_to_channel_impl();

      // Where all the action really happens
      int work(int noutput_items,
	       gr_vector_const_void_star &input_items,
	       gr_vector_void_star &output_items);
    };

  } // namespace multihop
} // namespace gr

#endif /* INCLUDED_MULTIHOP_FREQ_TO_CHANNEL_IMPL_H */

