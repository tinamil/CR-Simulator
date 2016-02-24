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

#ifndef INCLUDED_MULTIHOP_FREQHOP_SIGNAL_SOURCE_IMPL_H
#define INCLUDED_MULTIHOP_FREQHOP_SIGNAL_SOURCE_IMPL_H

#include <multihop/freqhop_signal_source.h>
#include <cmath>

namespace gr {
  namespace multihop {

    class freqhop_signal_source_impl : public freqhop_signal_source
    {
     private:
      double		d_sampling_freq;
      double		d_ampl;
      double		d_frequency;
      double    d_freqhop;
      gr_complex		d_offset;
      unsigned long      time;
      unsigned int interp_rate;
      const static double D_PI = 3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679821480865132823066470938446095505822317253594081284811174502841027019;

     public:
      freqhop_signal_source_impl(double sampling_freq, double ampl, gr_complex offset, double freqhop_rate);
      ~freqhop_signal_source_impl();
     

      // Where all the action really happens
      int work(int noutput_items,
	       gr_vector_const_void_star &input_items,
	       gr_vector_void_star &output_items);
    };

  } // namespace multihop
} // namespace gr

#endif /* INCLUDED_MULTIHOP_FREQHOP_SIGNAL_SOURCE_IMPL_H */

