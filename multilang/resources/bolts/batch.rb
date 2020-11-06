require_relative "../storm"

class SplitSentenceBolt < Storm::Bolt
  @count = 0
  class << self
    attr_accessor :count
  end

  def process(tup)
    begin
      a = tup.attributes.to_s
      vals = tup.values[0]
      if vals.nil?
        emit(["nil"])
      else
        logInfo(tup)
        if tup.tick?
          emit([SplitSentenceBolt.count])
        else
          vals.split(" ").each do |word|
            SplitSentenceBolt.count = SplitSentenceBolt.count + 1
          end
        end
      end
    rescue Exception => e
      emit([e.message])
    end
  end
end

SplitSentenceBolt.new.run
