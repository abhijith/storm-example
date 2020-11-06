require_relative "../storm"

class MarkBolt < Storm::Bolt
  def process(tup)
    emit(["---- #{tup.values.first} ----"])
  end
end

MarkBolt.new.run
