require_relative "../storm"

class CapitalizeBolt < Storm::Bolt
  def process(tup)
    xs = tup.values.first.map(&:capitalize)
    emit([xs])
  end
end

CapitalizeBolt.new.run
