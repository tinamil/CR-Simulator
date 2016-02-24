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

#ifndef INCLUDED_MULTIHOP_MAX_CF_IMPL_H
#define INCLUDED_MULTIHOP_MAX_CF_IMPL_H

#include <multihop/max_cf.h>

namespace gr {
  namespace multihop {

    class max_cf_impl : public max_cf
    {
     private:
      int vec_size;

     public:
      max_cf_impl(int vec_size);
      ~max_cf_impl();
      
      //Find the magnitude squared (real^2 + imag^2)) of a complex number
      float magnitude2(gr_complex val);

      // Where all the action really happens
      int work(int noutput_items,
	       gr_vector_const_void_star &input_items,
	       gr_vector_void_star &output_items);
    };

  } // namespace multihop
} // namespace gr

#endif /* INCLUDED_MULTIHOP_MAX_CF_IMPL_H */

