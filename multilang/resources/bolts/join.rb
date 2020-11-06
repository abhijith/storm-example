require_relative "../storm"

class Foo < Storm::Bolt
  @table = Hash.new {|h, k| h[k] = {} }
  class << self
    attr_accessor :table
  end

  def process(tup)
    case tup.stream
    when "name-stream"
      k = :name
    when "nickname-stream"
      k = :nickname
    else
      k = nil
    end
    if k
      id, v = tup.values
      Foo.table[id].update(k => v)
      emit([Foo.table])
    end
  end

end

Foo.new.run
