require_relative "../storm"

class CountBolt < Storm::Bolt
  def process(tup)
    emit([tup.values.first.count])
  end
end

CountBolt.new.run
