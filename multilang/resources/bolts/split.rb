require_relative "../storm"

class SplitBolt < Storm::Bolt
  def process(tup)
    emit([tup.values.first.split(" ")])
  end
end

SplitBolt.new.run
