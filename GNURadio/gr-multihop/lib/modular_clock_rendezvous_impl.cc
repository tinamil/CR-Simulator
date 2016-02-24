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

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include <gnuradio/io_signature.h>
#include "modular_clock_rendezvous_impl.h"

namespace gr {
  namespace multihop {

    modular_clock_rendezvous::sptr
    modular_clock_rendezvous::make(bool transmitter, int num_channels)
    {
      return gnuradio::get_initial_sptr
        (new modular_clock_rendezvous_impl(transmitter, num_channels));
    }

    /*
     * The private constructor
     */
    modular_clock_rendezvous_impl::modular_clock_rendezvous_impl(bool p_transmitter, int p_nchannels)
      : gr::block("modular_clock_rendezvous",
              gr::io_signature::make(0, 1, sizeof(int)),
              gr::io_signature::make(0, 1, sizeof(int)))
    {
      srand(getSeed());
      transmitter = p_transmitter;
      num_channels = p_nchannels;
      round = 0;
      prime = 0;
      while(prime == 0){
        prime = primeFind(num_channels, num_channels * 2);
      }
      index = rand() % num_channels;
      rate = (rand() % (num_channels - 2)) + 2;
      
      count = 0;
    }

    /*
     * Our virtual destructor.
     */
    modular_clock_rendezvous_impl::~modular_clock_rendezvous_impl()
    {
    }

    void
    modular_clock_rendezvous_impl::forecast (int noutput_items, gr_vector_int &ninput_items_required)
    {
      if(transmitter){
        ninput_items_required[0] = 0;
      }
      else{
        ninput_items_required[0] = noutput_items;
      }
    }
    
    unsigned int 
    modular_clock_rendezvous_impl::getSeed(){
      FILE *file = fopen("/dev/random", "r");
      unsigned int temp;
      int size = fread(&temp, 4, 1, file);
      fclose(file);
      return temp;
    }
    
    int modular_clock_rendezvous_impl::gcd(int a, int b){
      int c;
      while(true){
        c = a%b;
        if(c==0) return b;
        a = b;
        b = c;
      }
    }
    
    bool modular_clock_rendezvous_impl::primeCheck(int x){
      for(int i = 2; i <= int(sqrt(x)); ++i){
        if(gcd(i,x) > 1) return false;
      }
      return true;
    }
    
    int modular_clock_rendezvous_impl::primeFind(int x, int bound){
      while(x < bound){
        if(primeCheck(x)) return x;
        x++;
      }
      return 0;
    }
    
    int
    modular_clock_rendezvous_impl::general_work (int noutput_items,
                       gr_vector_int &ninput_items,
                       gr_vector_const_void_star &input_items,
                       gr_vector_void_star &output_items)
    {

        if(transmitter){
          int *out = (int *) output_items[0];
          out[0] = next_channel();
          return 1;
        }else{
           const int *in = (const int *) input_items[0];
           for(int i = 0; i < ninput_items[0]; ++i){
             int received_channel = in[i];
             int guessed_channel = next_channel();
             if(received_channel == guessed_channel){
                std::cout << "DONE AT " << count << std::endl;
             }
          }
          consume_each (ninput_items[0]);
          return 0;
        }
      
    }
    
    int modular_clock_rendezvous_impl::next_channel(){
      round += 1;
      count += 1;
      if(round > 2 * prime){
        round = 0;
        prime = 0;
        while(prime == 0){
          prime = primeFind((rand() % num_channels) + num_channels, num_channels * 2);
        }
        rate = (rand() % (num_channels - 2)) + 2;
      }
      index = fmod((index + rate), prime);
      if(index >= num_channels){
        srand(getSeed());
        index = rand() % num_channels;
      }
      return index;
    }

  } /* namespace multihop */
} /* namespace gr */

