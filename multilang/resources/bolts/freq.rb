require_relative "../storm"

class FreqBolt < Storm::Bolt
  @freq = Hash.new {|h, k| h[k] = 0 }
  class << self
    attr_accessor :freq
  end

  def process(tup)
    word = tup.values.first
    FreqBolt.freq[word] += 1
    emit([FreqBolt.freq])
  end
end

FreqBolt.new.run
