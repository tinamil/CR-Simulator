/* -*- c++ -*- */
/* 
 * Copyright 2016 <+YOU OR YOUR COMPANY+>.
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

#ifndef INCLUDED_MULTIHOP_MODULAR_CLOCK_RENDEZVOUS_IMPL_H
#define INCLUDED_MULTIHOP_MODULAR_CLOCK_RENDEZVOUS_IMPL_H

#include <multihop/modular_clock_rendezvous.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>

namespace gr {
  namespace multihop {

    class modular_clock_rendezvous_impl : public modular_clock_rendezvous
    {
     private:
      int num_channels;
      bool transmitter;
      
      int index;
      int prime;
      int rate;
      int round;
      
      int count;

     public:
      modular_clock_rendezvous_impl(bool transmitter, int num_channels);
      ~modular_clock_rendezvous_impl();

      // Where all the action really happens
      void forecast (int noutput_items, gr_vector_int &ninput_items_required);

      int general_work(int noutput_items,
		       gr_vector_int &ninput_items,
		       gr_vector_const_void_star &input_items,
		       gr_vector_void_star &output_items);
		       
		       
      unsigned int getSeed();
      int gcd(int a, int b);
      bool primeCheck(int x);
      int primeFind(int x, int bound);    
      int next_channel();
    };
    
    

  } // namespace multihop
} // namespace gr

#endif /* INCLUDED_MULTIHOP_MODULAR_CLOCK_RENDEZVOUS_IMPL_H */

