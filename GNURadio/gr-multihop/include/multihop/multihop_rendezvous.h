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


#ifndef INCLUDED_MULTIHOP_MULTIHOP_RENDEZVOUS_H
#define INCLUDED_MULTIHOP_MULTIHOP_RENDEZVOUS_H

#include <multihop/api.h>
#include <gnuradio/sync_block.h>

namespace gr {
  namespace multihop {

    /*!
     * \brief <+description of block+>
     * \ingroup multihop
     *
     */
    class MULTIHOP_API multihop_rendezvous : virtual public gr::sync_block
    {
     public:
      typedef boost::shared_ptr<multihop_rendezvous> sptr;

      /*!
       * \brief Return a shared_ptr to a new instance of multihop::multihop_rendezvous.
       *
       * To avoid accidental use of raw pointers, multihop::multihop_rendezvous's
       * constructor is in a private implementation
       * class. multihop::multihop_rendezvous::make is the public interface for
       * creating new instances.
       */
      static sptr make(int seed, double freq_rate, double samp_rate, int num_channels, int window_size, int search_speed);
    };

  } // namespace multihop
} // namespace gr

#endif /* INCLUDED_MULTIHOP_MULTIHOP_RENDEZVOUS_H */

