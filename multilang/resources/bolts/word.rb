require_relative "../storm"

class WordBolt < Storm::Bolt
  def process(tup)
    tup.values.first.each do |word|
      emit([word])
    end
  end
end

WordBolt.new.run
