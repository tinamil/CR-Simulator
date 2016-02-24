/* -*- c++ -*- */

#define MULTIHOP_API

%include "gnuradio.i"			// the common stuff

//load generated python docstrings
%include "multihop_swig_doc.i"

%{
#include "multihop/channel_selector.h"
#include "multihop/max_cf.h"
#include "multihop/freqhop_signal_source.h"
#include "multihop/csprng.h"
#include "multihop/mode_ii.h"
#include "multihop/channel_to_freq.h"
#include "multihop/freq_to_channel.h"
#include "multihop/multihop_rendezvous.h"
#include "multihop/modular_clock_rendezvous.h"
%}


%include "multihop/channel_selector.h"
GR_SWIG_BLOCK_MAGIC2(multihop, channel_selector);

%include "multihop/max_cf.h"
GR_SWIG_BLOCK_MAGIC2(multihop, max_cf);



%include "multihop/freqhop_signal_source.h"
GR_SWIG_BLOCK_MAGIC2(multihop, freqhop_signal_source);

%include "multihop/csprng.h"
GR_SWIG_BLOCK_MAGIC2(multihop, csprng);

%include "multihop/mode_ii.h"
GR_SWIG_BLOCK_MAGIC2(multihop, mode_ii);
%include "multihop/channel_to_freq.h"
GR_SWIG_BLOCK_MAGIC2(multihop, channel_to_freq);
%include "multihop/freq_to_channel.h"
GR_SWIG_BLOCK_MAGIC2(multihop, freq_to_channel);
%include "multihop/multihop_rendezvous.h"
GR_SWIG_BLOCK_MAGIC2(multihop, multihop_rendezvous);

%include "multihop/modular_clock_rendezvous.h"
GR_SWIG_BLOCK_MAGIC2(multihop, modular_clock_rendezvous);

