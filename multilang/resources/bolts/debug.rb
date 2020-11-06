require_relative "../storm"

class DebugBolt < Storm::Bolt
  def process(tup)
    emit([tup.attributes])
  end
end

DebugBolt.new.run
